CREATE TABLE account (
                         id SERIAL PRIMARY KEY,
                         username VARCHAR NOT NULL,
                         email VARCHAR NOT NULL UNIQUE,
                         phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE ticket (
                        id SERIAL PRIMARY KEY,
                        session_id INT NOT NULL,
                        row INT NOT NULL ,
                        cell INT NOT NULL ,
                        account_id INT NOT NULL REFERENCES account(id),
                        UNIQUE(session_id, row, cell)
);


INSERT INTO account(username,email,phone) VALUES('Alex','email-1','1122334455');
INSERT INTO account(username,email,phone) VALUES('Petr','email-2','5544332211');
INSERT INTO account(username,email,phone) VALUES('John','email-3','66778899');

INSERT INTO ticket(id,session_id,row,cell,account_id) VALUES('11','1','1','1','3');
INSERT INTO ticket(id,session_id,row,cell,account_id) VALUES('12','1','1','2','2');
INSERT INTO ticket(id,session_id,row,cell,account_id) VALUES('13','1','1','3','1');