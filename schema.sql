drop table readings;
drop table measurements;
drop table sensors;

create table sensors(id int not null, name varchar(32) not null, color varchar(16) not null, kind varchar(32) not null, port int, primary key (id));
create table measurements(id int not null primary key generated always as identity (start with 1, increment by 1), moment timestamp not null);
create table readings(measurement int not null, sensor int not null, temperature double, humidity double, primary key(measurement, sensor), foreign key (measurement) references measurements(id), foreign key (sensor) references sensors(id) );

insert into sensors(id, name, color, kind) values (1, 'Raspberry', '#666666', 'CPU');
insert into sensors(id, name, color, kind, port) values (2, 'Maschinenraum', '#71893F', 'DHT22', 5);
insert into sensors(id, name, color, kind, port) values (3, 'Werkraum', '#517199', 'DHT22', 4);
insert into sensors(id, name, color, kind, port) values (4, 'Außen', '#7B219F', 'DHT22', 17);
