// addPrescription.js
import { savePrescription, getPrescription } from "./services/prescriptionServices.js";

document.addEventListener('DOMContentLoaded', async () => {
    const savePrescriptionBtn = document.getElementById("savePrescription");
    const patientNameInput = document.getElementById("patientName");
    const medicinesInput = document.getElementById("medicines");
    const dosageInput = document.getElementById("dosage");
    const notesInput = document.getElementById("notes");
    const heading = document.getElementById("heading");

    const urlParams = new URLSearchParams(window.location.search);
    const appointmentId = urlParams.get("appointmentId");
    const mode = urlParams.get("mode");
    const token = localStorage.getItem("token");
    const patientName = urlParams.get("patientName");

    // Configurar el encabezado según el modo
    if (heading) {
        heading.innerHTML = (mode === "view") ? `View <span>Prescription</span>` : `Add <span>Prescription</span>`;
    }

    // Pre-rellenar nombre del paciente desde la URL
    if (patientNameInput && patientName) {
        patientNameInput.value = decodeURIComponent(patientName);
    }

    // Cargar receta existente si estamos en modo "view" o si existe una previa
    if (appointmentId && token) {
        try {
            const response = await getPrescription(appointmentId, token);
            console.log("Respuesta de getPrescription:", response);

            // Acceder a la receta (manejando el formato de Map/Array que vimos antes)
            const prescriptionList = response.prescription || [];
            if (prescriptionList.length > 0) {
                const existing = prescriptionList[0];
                
                // Usamos los nombres de campos que espera tu backend
                if (patientNameInput) patientNameInput.value = existing.patientName || patientName || "";
                if (medicinesInput) medicinesInput.value = existing.medication || "";
                if (dosageInput) dosageInput.value = existing.dosage || "";
                if (notesInput) notesInput.value = existing.doctorNotes || "";
            }
        } catch (error) {
            console.warn("No se encontró receta previa o error de carga:", error);
        }
    }

    // Configuración del modo LECTURA
    if (mode === 'view') {
        [patientNameInput, medicinesInput, dosageInput, notesInput].forEach(el => {
            if (el) el.disabled = true;
        });
        if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
    }

    // Guardar receta
    if (savePrescriptionBtn) {
        savePrescriptionBtn.addEventListener('click', async (e) => {
            e.preventDefault();

            // Validar campos mínimos
            if (!medicinesInput.value || !dosageInput.value) {
                alert("Please fill in the medication and dosage.");
                return;
            }

            const prescription = {
                patientName: patientNameInput.value,
                medication: medicinesInput.value,
                dosage: dosageInput.value,
                doctorNotes: notesInput.value,
                appointmentId: parseInt(appointmentId) // Asegurar que sea número
            };

            const result = await savePrescription(prescription, token);

            if (result.success) {
                alert("✅ Prescription saved successfully.");
                // Usar la función global de redirección
                if (window.selectRole) {
                    window.selectRole('doctor');
                } else {
                    window.location.href = "/pages/doctorDashboard.html";
                }
            } else {
                alert("❌ Failed to save: " + (result.message || "Unknown error"));
            }
        });
    }
});