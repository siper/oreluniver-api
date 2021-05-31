package model

import java.lang.Exception

abstract class ApiException(override val message: String?) : Exception(message)

class EntityNotFoundException : ApiException("Запрашиваемя сущность не найдена")