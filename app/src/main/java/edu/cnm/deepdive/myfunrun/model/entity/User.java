package edu.cnm.deepdive.myfunrun.model.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * The type User.
 */
@Entity(
    indices = @Index(value = "display_name", unique = true)
)
public class User {

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "user_id")
  private long id;

  @NonNull
  @ColumnInfo(name = "display_name")
  private String displayName;

  @ColumnInfo(name = "skill_level", index = true)
  private int skillLevel;

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
   * Gets display name.
   *
   * @return the display name
   */
  @NonNull
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Sets display name.
   *
   * @param displayName the display name
   */
  public void setDisplayName(@NonNull String displayName) {
    this.displayName = displayName;
  }

  /**
   * Gets skill level.
   *
   * @return the skill level
   */
  public int getSkillLevel() {
    return skillLevel;
  }

  /**
   * Sets skill level.
   *
   * @param skillLevel the skill level
   */
  public void setSkillLevel(int skillLevel) {
    this.skillLevel = skillLevel;
  }
}
