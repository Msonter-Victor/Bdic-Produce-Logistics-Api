truncate table _users cascade;
truncate table user_roles cascade;


insert into _users(created_at, id, email, first_name, last_name, password, phone)values
('2025-02-21T15:03:03.792009700','1caaabfc-31f3-4ccf-b449-a2cd91c6e145', 'hunchogrey73@gmail.com', 'Huncho', 'Grey', '$2a$10$seAKbpBsTn/xgAg7nbRKWuH1dnRvMlLloxMOjH00zMmTu3vLCtlee', '09022570223'),
('2025-02-21T15:03:02.792009700','1caaabfc-31f3-4ccf-b449-a2cd91c6e146', 'casandraghalen@gmail.com', 'Casandra', 'Ghalen', '$2a$10$seAKbpBsTn/xgAg7nbRKWuH1dnRvMlLloxMOjH00zMmTu3vLCtlee', '08143538183')
