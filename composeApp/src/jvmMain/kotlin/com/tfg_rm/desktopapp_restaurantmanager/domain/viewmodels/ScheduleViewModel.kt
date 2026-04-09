package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ScheduleService
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

sealed class SaveScheduleState {
    object Idle : SaveScheduleState()
    object Loading : SaveScheduleState()
    object Success : SaveScheduleState()
    data class Error(val message: String) : SaveScheduleState()
}

class ScheduleViewModel(
    private val scheduleService: ScheduleService,
    private val employeesService: EmployeesService
) : ViewModel() {

    private val _title = MutableStateFlow(Strings.t("screen.schedule.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _currentWeekStart = MutableStateFlow(LocalDate.now().with(DayOfWeek.MONDAY))
    val currentWeekStart: StateFlow<LocalDate> = _currentWeekStart.asStateFlow()

    private val _saveState = MutableStateFlow<SaveScheduleState>(SaveScheduleState.Idle)
    val saveState: StateFlow<SaveScheduleState> = _saveState.asStateFlow()

    /** Employee codes that have been edited (but not yet saved) for the currently visible week. */
    private val _unsavedEmployeeCodes = MutableStateFlow<Set<String>>(emptySet())
    val unsavedEmployeeCodes: StateFlow<Set<String>> = _unsavedEmployeeCodes.asStateFlow()

    /** True while the "unsaved changes" warning dialog should be visible. */
    private val _showUnsavedWarning = MutableStateFlow(false)
    val showUnsavedWarning: StateFlow<Boolean> = _showUnsavedWarning.asStateFlow()

    /** Week offset (±1) waiting to be applied once the user resolves the warning. */
    private var pendingNavigationDelta: Long = 0L

    // ── Loading ──────────────────────────────────────────────────────────────

    fun loadSchedule() {
        viewModelScope.launch {
            val emps = employeesService.getEmployees()
            _employees.value = emps
            _shifts.value = emps.flatMap { it.shifts }
            _unsavedEmployeeCodes.value = emptySet()
        }
    }

    // ── Shift editing (local state only – no network call) ───────────────────

    fun setShift(shift: Shift) {
        _shifts.value = _shifts.value
            .filter {
                !(it.employeeRestaurantId == shift.employeeRestaurantId &&
                    it.startDateTime.toLocalDate() == shift.startDateTime.toLocalDate())
            }
            .plus(shift)
        // Mark employee code as unsaved (we send schedules by code)
        val code = _employees.value.find { it.id == shift.employeeRestaurantId }?.code
        if (!code.isNullOrBlank()) _unsavedEmployeeCodes.value = _unsavedEmployeeCodes.value + code
    }

    fun removeShift(employeeRestaurantId: Int, day: DayOfWeek) {
        val shiftDate = _currentWeekStart.value.plusDays((day.value - 1).toLong())
        _shifts.value = _shifts.value.filter {
            !(it.employeeRestaurantId == employeeRestaurantId &&
                it.startDateTime.toLocalDate() == shiftDate)
        }
        val code2 = _employees.value.find { it.id == employeeRestaurantId }?.code
        if (!code2.isNullOrBlank()) _unsavedEmployeeCodes.value = _unsavedEmployeeCodes.value + code2
    }

    // ── Week navigation with unsaved-changes guard ───────────────────────────

    fun nextWeek() {
        if (_unsavedEmployeeCodes.value.isNotEmpty()) {
            pendingNavigationDelta = 1L
            _showUnsavedWarning.value = true
        } else {
            _currentWeekStart.value = _currentWeekStart.value.plusWeeks(1)
        }
    }

    fun prevWeek() {
        if (_unsavedEmployeeCodes.value.isNotEmpty()) {
            pendingNavigationDelta = -1L
            _showUnsavedWarning.value = true
        } else {
            _currentWeekStart.value = _currentWeekStart.value.minusWeeks(1)
        }
    }

    /** User chose to discard changes and move to the pending week. */
    fun discardAndNavigate() {
        _unsavedEmployeeCodes.value = emptySet()
        _showUnsavedWarning.value = false
        _currentWeekStart.value = _currentWeekStart.value.plusWeeks(pendingNavigationDelta)
        pendingNavigationDelta = 0L
    }

    /** User cancelled the navigation; stay on current week. */
    fun cancelNavigation() {
        _showUnsavedWarning.value = false
        pendingNavigationDelta = 0L
    }

    /** Save current week's dirty shifts, then navigate to the pending week. */
    fun saveAndNavigate() {
        val delta = pendingNavigationDelta
        pendingNavigationDelta = 0L
        _showUnsavedWarning.value = false
        viewModelScope.launch {
            _saveState.value = SaveScheduleState.Loading
            runCatching { doSave() }
                .onSuccess {
                    _saveState.value = SaveScheduleState.Success
                    _unsavedEmployeeCodes.value = emptySet()
                    _currentWeekStart.value = _currentWeekStart.value.plusWeeks(delta)
                }
                .onFailure {
                    _saveState.value = SaveScheduleState.Error(
                        "Error al guardar horarios. Comprueba la conexión."
                    )
                }
        }
    }

    // ── Save ─────────────────────────────────────────────────────────────────

    fun saveSchedule() {
        viewModelScope.launch {
            _saveState.value = SaveScheduleState.Loading
            runCatching { doSave() }
                .onSuccess {
                    _saveState.value = SaveScheduleState.Success
                    _unsavedEmployeeCodes.value = emptySet()
                }
                .onFailure {
                    _saveState.value = SaveScheduleState.Error(
                        "Error al guardar horarios. Comprueba la conexión."
                    )
                }
        }
    }

    private suspend fun doSave() {
        val weekStart = _currentWeekStart.value
        val weekEnd   = weekStart.plusDays(6)
        val dirtyCodes  = _unsavedEmployeeCodes.value
        if (dirtyCodes.isEmpty()) return

        // Only employees that have been modified AND have a valid code for the endpoint
        val dirtyEmployees = _employees.value.filter { it.code in dirtyCodes && it.code.isNotBlank() }
        if (dirtyEmployees.isEmpty()) return

        val weekShifts = _shifts.value.filter {
            val date = it.startDateTime.toLocalDate()
            !date.isBefore(weekStart) && !date.isAfter(weekEnd)
        }

        scheduleService.saveEmployeeSchedules(
            employees = dirtyEmployees,
            weekStart = weekStart,
            shifts = weekShifts
        )
    }

    // ── Misc ─────────────────────────────────────────────────────────────────

    fun resetSaveState() {
        _saveState.value = SaveScheduleState.Idle
    }

    fun clear() {}
}
