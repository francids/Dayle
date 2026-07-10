package fyi.dayle.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtil {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): String = LocalDate.now().format(formatter)
}
