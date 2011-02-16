alter table person
add column firstName varchar(255);

alter table person
add column lastName varchar(255);

alter table person
drop column name;

alter table person
add column birtDate date;
