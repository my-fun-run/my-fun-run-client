package edu.cnm.deepdive.myfunrun.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

// TODO Don't forget to reconstruct DDL
@Entity(
    foreignKeys = {
        @ForeignKey(
            entity = User.class, parentColumns = "user_id", childColumns = "author_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = History.class, parentColumns = "history_id", childColumns = "history_id",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class Comment  {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "comment_id")
  private long id;

  @ColumnInfo(name = "author_id", index = true)
  private Long authorId;

  @ColumnInfo(name = "history_id", index = true)
  private long historyId;

  private Date date; //TODO add to ERD and to server side model

  private String text;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  public long getHistoryId() {
    return historyId;
  }

  public void setHistoryId(long historyId) {
    this.historyId = historyId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
