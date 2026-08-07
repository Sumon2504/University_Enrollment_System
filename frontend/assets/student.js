const studentSession = UamsApi.requireRole("STUDENT");
document.getElementById("current-user").textContent = studentSession?.fullName || "";
const studentId = studentSession?.studentId;
let studentData = { profile: null, courses: [], enrollments: [], grades: [] };

function showStudentMessage(text, success = false) {
  const element = document.getElementById("message"); element.textContent = text; element.className = success ? "message success" : "message";
}
function td(row, value) { const cell = row.insertCell(); cell.textContent = value ?? ""; return cell; }
function courseName(id) { const course = studentData.courses.find(item => item.courseId === id); return course ? `${course.courseCode} - ${course.courseName}` : id; }

async function loadStudent() {
  if (!studentId) return showStudentMessage("This account is not linked to a student record.");
  try {
    const [profile, courses, enrollments, grades] = await Promise.all([
      UamsApi.request(`/students/${studentId}`), UamsApi.request("/courses"), UamsApi.request(`/enrollments?studentId=${studentId}`), UamsApi.request(`/grades/student/${studentId}`)
    ]);
    studentData = { profile, courses, enrollments, grades }; fillProfile(); renderStudentTables();
  } catch (error) { showStudentMessage(error.message); }
}

function fillProfile() {
  const form = document.getElementById("profile-form");
  for (const name of ["name", "email", "contactNumber", "department", "enrollmentYear"]) form.elements[name].value = studentData.profile[name] ?? "";
}

function renderStudentTables() {
  const active = studentData.enrollments.filter(item => item.enrollmentStatus === "ENROLLED");
  const activeIds = active.map(item => item.courseId);
  const availableBody = document.getElementById("available"); availableBody.replaceChildren();
  studentData.courses.filter(item => !activeIds.includes(item.courseId)).forEach(course => {
    const row = availableBody.insertRow(); td(row, `${course.courseCode} - ${course.courseName}`); td(row, course.credits);
    const button = document.createElement("button"); button.className = "small"; button.textContent = "Enroll"; button.addEventListener("click", () => enroll(course.courseId)); row.insertCell().append(button);
  });
  const enrolledBody = document.getElementById("enrolled"); enrolledBody.replaceChildren();
  active.forEach(item => {
    const row = enrolledBody.insertRow(); td(row, courseName(item.courseId)); td(row, UamsApi.label(item.enrollmentStatus));
    const button = document.createElement("button"); button.className = "small danger"; button.textContent = "Drop"; button.addEventListener("click", () => drop(item.enrollmentId)); row.insertCell().append(button);
  });
  const gradeBody = document.getElementById("grades"); gradeBody.replaceChildren();
  studentData.grades.forEach(item => { const row = gradeBody.insertRow(); td(row, courseName(item.courseId)); td(row, item.grade); td(row, item.remarks); });
}

async function enroll(courseId) {
  try { await UamsApi.request("/enrollments", { method: "POST", body: JSON.stringify({ studentId, courseId }) }); showStudentMessage("Course added.", true); await loadStudent(); } catch (error) { showStudentMessage(error.message); }
}
async function drop(enrollmentId) {
  try { await UamsApi.request(`/enrollments/${enrollmentId}/drop`, { method: "PUT" }); showStudentMessage("Course dropped.", true); await loadStudent(); } catch (error) { showStudentMessage(error.message); }
}

document.getElementById("profile-form").addEventListener("submit", async event => {
  event.preventDefault(); const form = event.currentTarget;
  const profile = { ...studentData.profile, name: form.elements.name.value.trim(), email: form.elements.email.value.trim(), contactNumber: form.elements.contactNumber.value.trim() };
  try { await UamsApi.request(`/students/${studentId}`, { method: "PUT", body: JSON.stringify(profile) }); showStudentMessage("Profile saved.", true); await loadStudent(); } catch (error) { showStudentMessage(error.message); }
});

document.getElementById("download-transcript").addEventListener("click", async () => {
  try { await UamsApi.download(`/transcripts/${studentId}/pdf`, `student-${studentId}-grade-sheet.pdf`); }
  catch (error) { showStudentMessage(error.message); }
});

loadStudent();
