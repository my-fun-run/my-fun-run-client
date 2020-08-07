package edu.cnm.deepdive.myfunrun.model.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *  This entity class contains static methods, with convenience annotations,
 * which provides additional information about event to assist Hibernate in mapping
 * an entity class field to a table column in a Apache Derby database, and retrieved.
 */
@Entity(
    indices = @Index(value = "name", unique = true)
)
public class Race {

  @Expose
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "race_id")
  private long id;

  @Expose
  @SerializedName("displayName")
  private String name;

  /**
   * Gets id.
   *
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  @NonNull
  @Override
  public String toString() {
    return name;
  }
}
