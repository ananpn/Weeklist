package com.weeklist.LogicAndData

//global functions

/*

fun roundToDecimalPlaces(number: Float, scale : Int): Float {
    val bd = BigDecimal(number.toDouble())
        .setScale(scale, RoundingMode.HALF_UP)
    return bd.toFloat()
}

@Composable
fun lastOrder(tasks : List<Task>) : Int {
    var output : Int = 0
    for (task in tasks){
        output = max(output, task.orderId)
    }
    return output
}

//@Composable
fun lastGroupOrder(groups : List<TaskGroup>) : Int {
    var output : Int = 0
    for (group in groups){
        output = max(output, group.groupOrder)
    }
    return output
}
*/


//database

/*@DatabaseView(
    "SELECT idTC, taskId, date, count FROM task_counts "
)
data class TaskCountView(
    val idTC: Int,
    val taskId : Int,
    val date: String,
    val count: Int
)*/

/*companion object {
    @Volatile
    private var Instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        // if the Instance is not null, return it, otherwise create a new database instance.
        return Instance ?: synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "task_database")
                .fallbackToDestructiveMigration()
                //.addCallback(AppDatabaseCallback(scope = CoroutineScope(Dispatchers.IO)))
                .build()
                .also { Instance = it }
        }
    }*/

/*

            private class AppDatabaseCallback(
                private val scope: CoroutineScope
            ) : RoomDatabase.Callback() {
                */
/**
 * Override the onCreate method to populate the database.
 *//*

                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // If you want to keep the data through app restarts,
                    // comment out the following line.
                    Instance?.let { database ->
                        scope.launch(Dispatchers.IO) {
                            populateDatabase(database.taskDao())
                        }
                    }
                }
            }

            suspend fun populateDatabase(taskDao: TaskDao) {
                // Start the app with a clean database every time.
                // Not needed if you only populate on creation.
                taskDao.deleteAllTasks()
                taskDao.deleteAllTaskCounts()
            }
        }
*/

/**
 * Populate the database in a new coroutine.
 * If you want to start with more words, just add them.
 */

/*
object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            instance = database
            database
        }
    }
}*/


//DataTypes
/*

data class DateCountWithGroupId(
    val date: String,
    val groupId: Int,
    val sum: Float
)

*/


//maintextBox

/*

@Composable
fun mainTextBox(textIn : String, order : Int){
    var fontSize by rememberSaveable { mutableStateOf(13.0) }
    var targetFontSize by rememberSaveable { mutableStateOf(normalFontSize) }
    var readyToDraw by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(textIn) }
    var inflated by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(textIn){
        text=textIn
    }
    LaunchedEffect(order){
        fontSize = 1.06*fontSize
        targetFontSize = 1.06*targetFontSize
        inflated = true
        text = textIn
        delay(80)
        fontSize = fontSize/1.06
        targetFontSize = targetFontSize/1.06
        inflated = false
    }
    Text(
        text = text,
        fontSize = fontSize.sp,
        maxLines = 2,
        softWrap = false,
        textAlign = TextAlign.Center,
        lineHeight = (1.8*fontSize).sp,
        modifier = Modifier
            .drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (fontSize>targetFontSize+.5) {
                fontSize = targetFontSize
            }
            if (textLayoutResult.didOverflowWidth) {
                text = newLineAtCenter(textIn)
                if (fontSize>9.0 && inflated) {
                    fontSize = fontSize * 0.85
                }
                if (fontSize>7.3 && !inflated) {
                    fontSize = fontSize * 0.85
                }
            }
            else {
                readyToDraw = true
            }
            if (fontSize<=7.3) readyToDraw = true
        },
    )
}
*/
