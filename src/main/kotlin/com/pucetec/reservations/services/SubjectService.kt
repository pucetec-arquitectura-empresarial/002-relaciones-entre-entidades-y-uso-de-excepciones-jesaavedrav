package com.pucetec.reservations.services

import com.pucetec.reservations.exceptions.ProfessorNotFoundException
import com.pucetec.reservations.exceptions.StudentAlreadyEnrolledException
import com.pucetec.reservations.exceptions.StudentNotFoundException
import com.pucetec.reservations.exceptions.SubjectNotFoundException
import com.pucetec.reservations.mappers.SubjectMapper
import com.pucetec.reservations.models.entities.Subject
import com.pucetec.reservations.models.requests.SubjectRequest
import com.pucetec.reservations.models.responses.SubjectResponse
import com.pucetec.reservations.repositories.ProfessorRepository
import com.pucetec.reservations.repositories.StudentRepository
import com.pucetec.reservations.repositories.SubjectRepository
import org.springframework.stereotype.Service

@Service
class SubjectService(
    private val subjectRepository: SubjectRepository,
    private val professorRepository: ProfessorRepository,
    private val studentRepository: StudentRepository,
    private val subjectMapper: SubjectMapper,
) {
    fun createSubject(request: SubjectRequest): SubjectResponse {
        val professor = professorRepository.findById(request.professorId)
            .orElseThrow { ProfessorNotFoundException("Profesor no encontrado con id: ${request.professorId}") }

        val subject = Subject(
            name = request.name,
            semester = request.semester,
            professor = professor
        )

        return subjectMapper.toResponse(subjectRepository.save(subject))
    }

    fun enrollStudent(subjectId: Long, studentId: Long): SubjectResponse {
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { SubjectNotFoundException("Asignatura no encontrada con id: $subjectId") }

        val student = studentRepository.findById(studentId)
            .orElseThrow { StudentNotFoundException("Estudiante no encontrado con id: $studentId") }

        if (subject.students.contains(student)) {
            throw StudentAlreadyEnrolledException("El estudiante ${student.name} ya está inscrito en la asignatura ${subject.name}")
        }

        subject.students.add(student)
        return subjectMapper.toResponse(subjectRepository.save(subject))
    }

    
    fun listSubjects(): List<SubjectResponse> =
        subjectMapper.toResponseList(subjectRepository.findAll())
}