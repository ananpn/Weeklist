package com.weeklist.di

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton


val Context.WeekListPrefs by preferencesDataStore("WeekListPrefs")

class WeekListPrefs(context: Context) : PrefsImpl {

    private val dataStore = context.WeekListPrefs

    // used to get the data from datastore
    override val darkTheme: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            val darkTheme = preferences[DARK_KEY] ?: false
            darkTheme
        }

    // used to save the ui preference to datastore
    override suspend fun saveDarkTheme(isNightMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_KEY] = isNightMode
        }
    }

    override val dispGroupId: Flow<Int>
    get() = dataStore.data.map { preferences ->
        val dispGroupId = preferences[GROUP_KEY] ?:0
        dispGroupId
    }

    override suspend fun saveDispGroupId(dispGroupId: Int) {
        dataStore.edit { preferences ->
            preferences[GROUP_KEY] = dispGroupId
        }
    }

    override val seedColorData: Flow<Float>
    get() = dataStore.data.map { preferences ->
        val seedColorData = preferences[SEED_COLOR_KEY] ?: 180f
        seedColorData
    }


    override suspend fun saveSeedColorData(newSeedColorData: Float) {
        dataStore.edit { preferences ->
            preferences[SEED_COLOR_KEY] = newSeedColorData
        }
    }

    override val paletteData: Flow<Int>
        get() = dataStore.data.map { preferences ->
            val paletteData = preferences[PALETTE_KEY] ?: 0
            paletteData
        }


    override suspend fun savePaletteData(newPaletteData: Int) {
        dataStore.edit { preferences ->
            preferences[PALETTE_KEY] = newPaletteData
        }
    }

    override val editPast: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            val editPast = preferences[EDIT_PAST_KEY] ?: false
            editPast
        }


    override suspend fun saveEditPast(newEditPast: Boolean) {
        dataStore.edit { preferences ->
            preferences[EDIT_PAST_KEY] = newEditPast
        }
    }
/*

    override val rowTapMode: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            val rowTapMode = preferences[ROW_TAP_MODE_KEY] ?: false
            rowTapMode
        }


    override suspend fun saveRowTapMode(newRowTapMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[ROW_TAP_MODE_KEY] = newRowTapMode
        }
    }
*/

    companion object {
        private val DARK_KEY = booleanPreferencesKey("dark_theme")
        private val SEED_COLOR_KEY = floatPreferencesKey("seed_color")
        private val GROUP_KEY = intPreferencesKey("dispGroupId")
        private val PALETTE_KEY = intPreferencesKey("palette")
        private val EDIT_PAST_KEY = booleanPreferencesKey("past_edit")
        //private val ROW_TAP_MODE_KEY = booleanPreferencesKey("row_tap_mode")
    }



}

@Singleton
interface PrefsImpl {

    val darkTheme: Flow<Boolean>

    suspend fun saveDarkTheme(isNightMode: Boolean)

    val seedColorData: Flow<Float>

    suspend fun saveSeedColorData(newSeedColorData : Float)

    val dispGroupId : Flow<Int>

    suspend fun saveDispGroupId(dispGroupId : Int)

    val paletteData : Flow<Int>

    suspend fun savePaletteData(paletteData : Int)

    val editPast : Flow<Boolean>

    suspend fun saveEditPast(editPast : Boolean)

    /*
    val rowTapMode : Flow<Boolean>

    suspend fun saveRowTapMode(rowTapMode : Boolean)
    */
}



/*
@Singleton
class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    suspend fun writeIntegerValue(value: Int) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.INTEGER_KEY] = value
        }
    }

    suspend fun writeStringValue(value: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.STRING_KEY] = value
        }
    }

    val integerValueFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DataStoreKeys.INTEGER_KEY] ?: 0
        }
}

object DataStoreKeys {
    val INTEGER_KEY = intPreferencesKey("integer_key")
    val STRING_KEY = stringPreferencesKey("string_key")
}
*/