INSERT INTO users(email, password, nickname, user_role, receive_updates)
VALUES ('test@test', '$2a$12$QcnVP4N0MtYrnHntGoIBEOzuu7S6739x/wIgPLEPICMiZ5Ws8XdB6', 'test', 'GUEST', true);
INSERT INTO users(email, password, nickname, user_role, receive_updates)
VALUES ('abc@test', '$2a$12$DQEP0nuU52/PWHiCxyCU0uznigOzmdfwassJJLryfogiAP3YPBoBa', 'abc', 'GUEST', true);

-- insert into refresh_token (device_address,expire_at,user_id) values ('0:0:0:0:0:0:0:1', '2024-04-29 12:20:26.125427+00', 1);