BEGIN TRANSACTION;
CREATE TABLE "material" (
	`_id`	INTEGER PRIMARY KEY AUTOINCREMENT ,
	`name`	TEXT NOT NULL,    
	`type`	TEXT NOT NULL,
	`image`	INTEGER NOT NULL,
	`image_grey`	INTEGER NOT NULL,   
        	`status`	TEXT NOT NULL,     
UNIQUE (`name`) ON CONFLICT REPLACE);
INSERT INTO `material` VALUES(1,'huanggua','su','R.mipmap.temp','R.mipmap.temp_grey','NO');
CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT 'en_US');
INSERT INTO `android_metadata` VALUES('en_US');
;
COMMIT;
