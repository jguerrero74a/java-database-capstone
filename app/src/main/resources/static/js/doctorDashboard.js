/* doctorDashboard.js - Gestión de Citas para el Médico */

// 1. Importar Módulos Requeridos
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// 2. Inicializar Variables Globales
const tableBody = document.getElementById('patientTableBody');
const token = localStorage.getItem('token');

// Obtener fecha de hoy en formato YYYY-MM-DD
let selectedDate = new Date().toISOString().split('T')[0];
let patientName = "none"; // Inicializado como "null" para el backend

/**
 * Configurar la funcionalidad de la Barra de Búsqueda
 */
const searchBar = document.getElementById('searchBar');
if (searchBar) {
    searchBar.addEventListener('input', (e) => {
        const value = e.target.value.trim();
        // Si está vacío, enviamos "null", de lo contrario el nombre
        patientName = value !== "" ? value : "none";
        loadAppointments();
    });
}

/**
 * Vincular Oyentes de Eventos a los Controles de Filtro
 */
const todayButton = document.getElementById('todayButton');
const datePicker = document.getElementById('datePicker');

// Botón "Hoy"
if (todayButton) {
    todayButton.addEventListener('click', () => {
        selectedDate = new Date().toISOString().split('T')[0];
        if (datePicker) datePicker.value = selectedDate;
        loadAppointments();
    });
}

// Selector de fecha
if (datePicker) {
    datePicker.value = selectedDate; // Sincronizar UI inicial
    datePicker.addEventListener('change', (e) => {
        selectedDate = e.target.value;
        loadAppointments();
    });
}

/**
 * Función: loadAppointments
 * Propósito: Obtener y mostrar citas basadas en la fecha y filtro de nombre.
 */

async function loadAppointments() {
    try {
        const data = await getAllAppointments(selectedDate, patientName, token);

        // EXTRAER LA LISTA: El Java envía un Map con la clave "appointments"
        const appointments = data.appointments || []; 

        tableBody.innerHTML = "";

        if (appointments.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center;">No se encontraron citas.</td></tr>`;
            return;
        }

        appointments.forEach(app => {
            // Mapeo según tu Appointment.java (asumiendo que tiene .patient e .id)
            // Extraemos también el doctorId del token o del objeto si viene ahí
            const row = createPatientRow(app.patient, app.id, app.doctor?app.doctor.id : ""); 
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error al cargar citas:", error);
        tableBody.innerHTML = `<tr><td colspan="5" style="color: red; text-align: center;">Error al cargar datos.</td></tr>`;
    }
}


/**
 * Renderizado Inicial al Cargar la Página
 */
document.addEventListener('DOMContentLoaded', () => {
    // Si existe una función para renderizar el layout base, se llama aquí
    if (typeof renderContent === 'function') {
        renderContent();
    }
    
    // Carga inicial de citas por defecto (Hoy)
    loadAppointments();
});