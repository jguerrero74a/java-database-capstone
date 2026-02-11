/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

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