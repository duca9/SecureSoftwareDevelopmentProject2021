INSERT INTO USERS(ID, USERNAME, PASSWORD)
values (1, 'bruce', 'wayne'),
       (2, 'peter', 'security_rules'),
       (3, 'tom', 'guessmeifyoucan');

INSERT INTO HASHEDUSERS(id, username, passwordHash, salt)
VALUES (1, 'bruce', 'qw8Uxa2fXimKruS9wYEm4qm3ZaIGw/hJNvOG3PemhoA=', 'MEI4PU5hcHhaRHZz'),
       (2, 'peter', 'qPWryBEWiWdHsC+67dmO+y5ugGrMVI2w4MSz0+CpDm4=', 'MnY1am14c2d1ZlBf'),
       (3, 'tom', 'FLmYMYmwSRxcy0n2uwysy39ax0TRWvKHswSCPMo+PiI=', 'OChoOitAKWE0TWlD');

INSERT INTO ADDRESS (id, name, userId)
VALUES (1, 'Gotham City, Bat cave', 1),
       (2, 'Beograd, Gazela', 1),
       (3, 'Beogradska industrija piva', 1),
       (4, 'Redwood', 2),
       (5, 'Trg Nikole Pasica 3', 2),
       (6, 'Batajnica, Srpskih vladara 25', 3);


INSERT INTO DELIVERY(id, isDone, userId, restaurantId, addressId, date, comment)
VALUES (1, TRUE, 2, 1, 1, '2021-12-2', 'Hurry up I am hungry'),
       (2, FALSE, 2, 1, 1, '2021-12-31', 'Just leave it at the door'),
       (3, FALSE, 2, 1, 1, '2021-12-31', '');

INSERT INTO FOOD(id, name, price, restaurantId)
VALUES (1, 'Cevapi', 750, 1),
       (2, 'Pljeskavica', 600, 1),
       (3, 'Kajmak', 100, 1),
       (4, 'Svadbarski kupus', 400, 1),
       (5, 'Becka snicla', 1000, 1),
       (6, 'Pizza Margaritha', 800, 2),
       (7, 'Pizza Vesuvio', 900, 2),
       (8, 'Pizza Quattro Formaggi', 850, 2),
       (9, 'Pizza Quattro Stagioni', 850, 2),
       (10, 'Pizza kulen', 900, 2);

INSERT INTO RESTAURANT_TYPE(id, name)
VALUES (1, 'restoran domace kuhinje'),
       (2, 'pizza bar');

INSERT INTO RESTAURANT(id, name, address, typeid)
VALUES (1, 'Moj zavicaj', 'Maksima Gorkog 12, Beograd', 1),
       (2, 'Pizza industrija', 'Obilicev venac 5, Beograd', 2);

INSERT INTO DELIVERY_ITEM(id, amount, foodId, deliveryId)
VALUES (1, 2, 1, 1),
       (2, 5, 3, 1),
       (3, 1, 4, 2),
       (4, 1, 5, 2),
       (5, 2, 10, 3);


INSERT INTO PERMISSIONS(ID, NAME)
VALUES (1, 'ORDER_FOOD'),
       (2, 'USERS_LIST_VIEW'),
       (3, 'USERS_DETAILS_VIEW'),
       (4, 'USERS_EDIT'),
       (5, 'USERS_DELETE'),
       (6, 'RESTAURANT_LIST_VIEW'),
       (7, 'RESTAURANT_DETAILS_VIEW'),
       (8, 'RESTAURANT_EDIT'),
       (9, 'RESTAURANT_DELETE'),
       (10, 'DELIVERY_LIST_VIEW'),
       (11, 'DELIVERY_DETAILS_VIEW');

INSERT INTO ROLES(ID, NAME)
VALUES (1, 'CUSTOMER'),
       (2, 'RESTAURANT'),
       (3, 'ADMIN');

INSERT INTO ROLE_TO_PERMISSIONS(ROLEID, PERMISSIONID)
VALUES
    /* CUSTOMER PERMISSION */
    (1, 1),
    (1, 6),
    /* RESTAURANT PERMISSION */
    (2, 2),
    (2, 3),
    (2, 6),
    (2, 7),
    (2, 8),
    (2, 10),
    (2, 11),
    /* ADMIN PERMISSION */
    (3, 1),
    (3, 2),
    (3, 3),
    (3, 4),
    (3, 5),
    (3, 6),
    (3, 7),
    (3, 8),
    (3, 9),
    (3, 10),
    (3, 11);

INSERT INTO USER_TO_ROLES(USERID, ROLEID)
VALUES (1, 1),
       (2, 2),
       (3, 3);