## Data Definition Language

```sqlite
CREATE TABLE IF NOT EXISTS `History`
(
    `history_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name`      TEXT COLLATE NOCASE
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_History_name` ON `History` (`name`);

CREATE TABLE IF NOT EXISTS `${TABLE_NAME}`
(
    `user_id`  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `history_id` INTEGER,
    `race_id`  INTEGER, 
    FOREIGN KEY (`user_id`) REFERENCES `History` (`name`) ON UPDATE NO ACTION ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS `index_MyFunRun_history_id` ON `${TABLE_NAME}` (`history_id`);
```

[`ddl.sql`](sql/ddl.sql)