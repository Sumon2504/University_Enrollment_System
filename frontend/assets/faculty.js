const session = UamsApi.requireRole("FACULTY");
document.getElementById("current-user").textContent = session?.fullName || "";
let data = { courses: [], students: [], enrollments: [], grades: [] };

function message(text, success = false) {
  const element = document.getElementById("message"); element.textContent = text;
  element.className = success ? "message success" : "message";
}
function cell(row, value) { const item = row.insertCell(); item.textContent = value ?? ""; return item; }

async function load() {
  try {
    const [courses, students, enrollments, grades] = await Promise.all([
      UamsApi.request("/courses"), UamsApi.request("/students"), UamsApi.request("/enrollments"), UamsApi.request("/grades")
    ]);
    data = { courses, students, enrollments, grades };
    const select = document.getElementById("course"); select.replaceChildren();
    courses.forEach(course => select.add(new Option(`${course.courseCode} - ${course.courseName}`, course.courseId)));
    renderRoster(); renderRecords();
  } catch (error) { message(error.message); }
}

function renderRoster() {
  const courseId = Number(document.getElementById("course").value);
  const activeIds = data.enrollments.filter(item => item.courseId === courseId && item.enrollmentStatus === "ENROLLED").map(item => item.studentId);
  const body = document.getElementById("roster"); body.replaceChildren();
  data.students.filter(student => activeIds.includes(student.studentId)).forEach(student => {
    const existing = data.grades.find(item => item.courseId === courseId && item.studentId === student.studentId);
    const row = body.insertRow(); cell(row, student.studentId); cell(row, student.name);
    const grade = document.createElement("input"); grade.value = existing?.grade || ""; grade.placeholder = "A"; grade.maxLength = 4; grade.setAttribute("aria-label", `Grade for ${student.name}`); row.insertCell().append(grade);
    const remarks = document.createElement("input"); remarks.value = existing?.remarks || ""; remarks.maxLength = 200; remarks.placeholder = "Optional remarks"; remarks.setAttribute("aria-label", `Remarks for ${student.name}`); row.insertCell().append(remarks);
    const actions = row.insertCell();
    const save = document.createElement("button"); save.className = "small"; save.textContent = "Save";
    save.addEventListener("click", () => saveGrade(existing, student.studentId, courseId, grade.value, remarks.value));
    const pdf = document.createElement("button"); pdf.className = "small secondary"; pdf.textContent = "Grade sheet PDF";
    pdf.addEventListener("click", () => downloadTranscript(student.studentId, student.name)); actions.append(save, pdf);
  });
}

function renderRecords() {
  const body = document.getElementById("records"); body.replaceChildren();
  data.grades.forEach(grade => {
    const student = data.students.find(item => item.studentId === grade.studentId);
    const course = data.courses.find(item => item.courseId === grade.courseId);
    const row = body.insertRow(); cell(row, student?.name || grade.studentId); cell(row, course?.courseCode || grade.courseId); cell(row, grade.grade); cell(row, grade.remarks);
  });
}

async function saveGrade(existing, studentId, courseId, grade, remarks) {
  if (!/^(A[+-]?|B[+-]?|C[+-]?|D|F|PASS|FAIL)$/i.test(grade.trim())) return message("Enter a valid grade: A-F, Pass, or Fail.");
  if (remarks.length > 200) return message("Remarks cannot exceed 200 characters.");
  try {
    await UamsApi.request(existing ? `/grades/${existing.gradeId}` : "/grades", {
      method: existing ? "PUT" : "POST", body: JSON.stringify({ studentId, courseId, grade: grade.trim(), remarks: remarks.trim() })
    });
    message("Grade saved.", true); await load();
  } catch (error) { message(error.message); }
}

async function downloadTranscript(studentId, studentName) {
  try { await UamsApi.download(`/transcripts/${studentId}/pdf`, `${studentName.replaceAll(" ", "-")}-grade-sheet.pdf`); }
  catch (error) { message(error.message); }
}

document.getElementById("course").addEventListener("change", renderRoster);
load();
