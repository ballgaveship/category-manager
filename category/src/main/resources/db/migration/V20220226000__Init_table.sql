create table CATEGORY
(
    "ID"        bigint      not null primary key auto_increment,
    "NAME"      varchar(50) not null,
    "PARENT_ID" bigint null,
    "CREATED_DATE"  datetime not null,
    "MODIFIED_DATE" datetime null
);