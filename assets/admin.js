const adminSession = UamsApi.requireRole("ADMIN");
const departments = ["COMPUTER_SCIENCE", "INFORMATION_TECHNOLOGY", "MECHANICAL_ENGINEERING", "CIVIL_ENGINEERING", "BUSINESS_ADMINISTRATION", "MATHEMATICS", "PHYSICS"];
document.getElementById("current-user").textContent = adminSession?.fullName || "";
document.querySelectorAll(".department").forEach(select => {
  const placeholder = new Option("Select department", "", true, true); placeholder.disabled = true; select.add(placeholder);
  departments.forEach(value => select.add(new Option(UamsApi.label(value), value)));
});

function showMessage(text, success = false) {
  const element = document.getElementById("message");
  element.textContent = text;
  element.className = success ? "message success" : "message";
}

function addCell(row, value) { const cell = row.insertCell(); cell.textContent = value ?? ""; return cell; }
function addDelete(row, action) {
  const button = document.createElement("button"); button.className = "small danger"; button.textContent = "Remove";
  button.addEventListener("click", action); row.insertCell().append(button);
}

async function loadAdmin() {
  try {
    const [stats, students, faculty, courses] = await Promise.all([
      UamsApi.request("/admin/stats"), UamsApi.request("/students"), UamsApi.request("/faculty"), UamsApi.request("/courses")
    ]);
    const statsBox = document.getElementById("stats"); statsBox.replaceChildren();
    Object.entries(stats).forEach(([name, value]) => { const box = document.createElement("div"); box.className = "stat"; const strong = document.createElement("strong"); strong.textContent = value; box.append(strong, UamsApi.label(name)); statsBox.append(box); });
    renderStudents(students); renderFaculty(faculty); renderCourses(courses);
  } catch (error) { showMessage(error.message); }
}

function renderStudents(items) {
  const body = document.getElementById("students"); body.replaceChildren();
  items.forEach(item => { const row = body.insertRow();// It is used to create a new row in the table body for eac student item.
     addCell(row, item.studentId); addCell(row, item.name); addCell(row, item.email); addCell(row, UamsApi.label(item.department)); addDelete(row, () => remove("/students/" + item.studentId)); });
}
function renderFaculty(items) {
  const body = document.getElementById("faculty"); body.replaceChildren();
  items.forEach(item => { const row = body.insertRow(); addCell(row, item.facultyId); addCell(row, item.name); addCell(row, item.email); addCell(row, UamsApi.label(item.department)); addDelete(row, () => remove("/faculty/" + item.facultyId)); });
}
function renderCourses(items) {
  const body = document.getElementById("courses"); body.replaceChildren();
  items.forEach(item => { const row = body.insertRow(); addCell(row, item.courseCode); addCell(row, item.courseName); addCell(row, UamsApi.label(item.department)); addCell(row, item.seats); addDelete(row, () => remove("/courses/" + item.courseId)); });
}
async function remove(path) {
  if (!confirm("Remove this record?")) return;
  try { await UamsApi.request(path, { method: "DELETE" }); showMessage("Record removed.", true); await loadAdmin(); } catch (error) { showMessage(error.message); }
}
function objectFrom(form) { return Object.fromEntries(new FormData(form).entries()); }

function showForm(formId) {
  document.querySelectorAll(".admin-form").forEach(form => { form.hidden = form.id !== formId; });
  document.querySelectorAll("[data-form]").forEach(button => button.classList.toggle("active", button.dataset.form === formId));
  if (formId) document.getElementById(formId).scrollIntoView({ behavior: "smooth", block: "start" });
}

document.querySelectorAll("[data-form]").forEach(button => button.addEventListener("click", () => showForm(button.dataset.form)));
document.querySelectorAll(".cancel-form").forEach(button => button.addEventListener("click", () => showForm(null)));

async function completeAddition(form, message, listId) {
  showMessage(message, true); form.reset(); showForm(null); await loadAdmin();
  document.getElementById(listId).scrollIntoView({ behavior: "smooth", block: "start" });
}

document.getElementById("student-form").addEventListener("submit", async event => {
  event.preventDefault(); const form = event.currentTarget; const data = objectFrom(form); data.enrollmentYear = Number(data.enrollmentYear) || null;
  try { const result = await UamsApi.request("/students", { method: "POST", body: JSON.stringify(data) }); await completeAddition(form, `Student created. Username: ${result.username}`, "students-list"); } catch (error) { showMessage(error.message); }
});
document.getElementById("faculty-form").addEventListener("submit", async event => {
  event.preventDefault(); const form = event.currentTarget;
  try { const result = await UamsApi.request("/faculty", { method: "POST", body: JSON.stringify(objectFrom(form)) }); await completeAddition(form, `Faculty created. Username: ${result.username}`, "faculty-list"); } catch (error) { showMessage(error.message); }
});
document.getElementById("course-form").addEventListener("submit", async event => {
  event.preventDefault(); const form = event.currentTarget; const data = objectFrom(form);
  for (const field of ["credits", "seats", "totalSemesters", "durationYears"]) data[field] = Number(data[field]);
  try { await UamsApi.request("/courses", { method: "POST", body: JSON.stringify(data) }); await completeAddition(form, "Course created successfully.", "courses-list"); } catch (error) { showMessage(error.message); }
});

loadAdmin();
