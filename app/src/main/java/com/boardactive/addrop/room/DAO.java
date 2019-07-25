package com.boardactive.addrop.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.boardactive.addrop.room.table.MessageEntity;

import java.util.List;

@Dao
public interface DAO {

    /* table message transaction ----------------------------------------------------------- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(MessageEntity message);

    @Query("DELETE FROM message WHERE id = :id")
    void deleteMessage(long id);

    @Query("DELETE FROM message")
    void deleteAllMessage();

    @Query("SELECT * FROM message ORDER BY dateCreated DESC LIMIT :limit OFFSET :offset")
    List<MessageEntity> getMessageByPage(int limit, int offset);

    @Query("SELECT * FROM message WHERE id = :id LIMIT 1")
    MessageEntity getMessage(long id);

    @Query("SELECT COUNT(id) FROM message WHERE isRead = 0")
    Integer getMessageUnreadCount();

    @Query("SELECT COUNT(id) FROM message")
    Integer getMessageCount();
}
