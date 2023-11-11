package compose.base.app.data.dataSource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import compose.base.app.data.dataSource.local.db.dao.UserDao
import compose.base.app.data.dataSource.local.db.entity.UserEntity

@Database(
    entities = [UserEntity::class], version = 1
)
abstract class UserDatabase : RoomDatabase() {
    abstract val dao: UserDao
}