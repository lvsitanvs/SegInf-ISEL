package isel.seginf.group3.tasks.repository

interface Transaction {
    val userRepository: UserRepository
    val taskRepository: TaskRepository

    fun rollback()
}