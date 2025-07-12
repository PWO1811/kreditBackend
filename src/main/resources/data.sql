-- src/main/resources/data.sql
INSERT INTO clients (data)
SELECT '{"fullName": "Иванов Иван Петрович", "passportNumber": "1238 567891", "birthDate": "1990-05-15", "income": 100000}'
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE data->>'passportNumber' = '1238 567891');

INSERT INTO clients (data)
SELECT '{"fullName":"Петрова Анна Алексеевна","passportNumber":"9876 543210","birthDate":"1985-11-20","income":150000}'
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE data->>'passportNumber' = '9876 543210');