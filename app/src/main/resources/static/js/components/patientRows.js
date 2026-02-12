// patientRows.js
export function createPatientRow(patient, appointmentId, doctorId) {
    if (!patient) return document.createElement("tr");

    const tr = document.createElement("tr");
    
    // Verificación de datos para evitar "undefined" en la UI
    const pName = patient.name || "N/A";
    const pPhone = patient.phone || "Sin teléfono";
    const pEmail = patient.email || "Sin correo";

    tr.innerHTML = `
        <td class="patient-id" style="cursor:pointer; color:blue; text-decoration:underline;">
            ${patient.id}
        </td>
        <td>${pName}</td>
        <td>${pPhone}</td>
        <td>${pEmail}</td>
        <td>
            <img src="../assets/images/addPrescriptionIcon/addPrescription.png" 
                 alt="Prescription" 
                 class="prescription-btn" 
                 style="cursor:pointer; width:25px;"
                 data-id="${patient.id}">
        </td>
    `;

    // 1. Navegar al Historial del Paciente
    tr.querySelector(".patient-id").addEventListener("click", () => {
        window.location.href = `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
    });

    // 2. Navegar a Crear Receta
    tr.querySelector(".prescription-btn").addEventListener("click", () => {
        // Codificamos el nombre por si tiene espacios o caracteres especiales
        const encodedName = encodeURIComponent(pName);
        window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${encodedName}`;
    });

    return tr;
}