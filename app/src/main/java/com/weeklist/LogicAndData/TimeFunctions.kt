package com.weeklist.LogicAndData

import androidx.compose.runtime.Composable
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimeFunctions {
    companion object {
        //takes format eg "hhmmddMMyyy" or "yyyy" and returns the current time in this format
        fun getTimeNow(format : String) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val current = LocalDateTime.now().format(formatter)
            return current
        }

        fun formatToString(date : LocalDate, format : String) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = date.format(formatter)
            return output
        }

        fun formatToLocalDate(time : String, format : String) : LocalDateTime {
            //Log.v("fTLC time format", time+" "+format)
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = LocalDateTime.parse(time, formatter)
            return output
        }

        fun formatStringToDisplay(input : String) : String {
            return input.takeLast(2)+"."+input.substring(4,6)
        }

        @Composable
        fun timeMillis(time : String) : Long {
            val date=formatToLocalDate(time.replace(" ", "0"),
                "HHmmddMMyyyy")
            val millis = date.toEpochSecond(ZoneId.systemDefault()
                                                    .getRules()
                                                    .getOffset(Instant.now())
                    )*1000
            //Log.v("timeMillis", time+" "+millis)
            return millis
        }

        fun generateWeek(monday: LocalDate): List<LocalDate> {
            val dateList = mutableListOf<LocalDate>()
            var currentDate = monday

            while (currentDate <= monday.plusDays(6)) {
                dateList.add(currentDate)
                currentDate = currentDate.plusDays(1)
            }

            return dateList
        }

        fun obtainCurrentWeek() : List<LocalDate>{
            val lastMonday = lastMonday()
            return generateWeek(lastMonday)
        }

        fun obtainCurrentDate() : String{
            return getTimeNow("yyyyMMdd")
        }

        fun lastMonday() : LocalDate{
            var output = LocalDate.now()
            while (output.dayOfWeek != DayOfWeek.MONDAY){
                output = output.minusDays(1)
            }
            return output
        }

        fun isCurrentDay(date : String) : Boolean {
            return (getTimeNow("yyyyMMdd") == date)
        }
    }
}