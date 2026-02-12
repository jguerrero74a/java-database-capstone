/* doctorDashboard.js - Gestión de Citas para el Médico */

// 1. Importar Módulos Requeridos
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// 2. Inicializar Variables Globales
const tableBody = document.getElementById('patientTableBody');
const token = localStorage.getItem('token');

// Obtener fecha de hoy en formato YYYY-MM-DD
let selectedDate = new Date().toISOString().split('T')[0];
let patientName = "null"; // Inicializado como "null" para el backend

/**
 * Configurar la funcionalidad de la Barra de Búsqueda
 */
const searchBar = document.getElementById('searchBar');
if (searchBar) {
    searchBar.addEventListener('input', (e) => {
        const value = e.target.value.trim();
        // Si está vacío, enviamos "null", de lo contrario el nombre
        patientName = value !== "" ? value : "null";
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
        // Paso 1: Obtener datos de la API
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        // Paso 2: Limpiar contenido existente
        tableBody.innerHTML = "";

        // Paso 3: Validar si existen citas
        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align: center;">No se encontraron citas para hoy.</td>
                </tr>`;
            return;
        }

        // Paso 4: Renderizar filas
        appointments.forEach(appointment => {
            // Extraer detalles para el componente de fila
            const row = createPatientRow(appointment);
            tableBody.appendChild(row);
        });

    } catch (error) {
        // Paso 5: Manejo de errores
        console.error("Error al cargar citas:", error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; color: red;">
                    Error cargando las citas. Inténtelo más tarde.
                </td>
            </tr>`;
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