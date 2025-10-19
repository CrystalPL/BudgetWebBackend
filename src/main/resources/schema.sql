DELIMITER //

CREATE PROCEDURE activate_filter(IN p_id BIGINT, IN p_requester_id BIGINT, OUT filter_exists BOOLEAN)
BEGIN_PROC:
BEGIN
    SELECT EXISTS(SELECT 1 FROM advanced_filter af WHERE af.id = p_id AND af.user_id = p_requester_id)
    INTO filter_exists;

    IF filter_exists IS FALSE THEN
        LEAVE BEGIN_PROC;
    END IF;

    UPDATE advanced_filter af SET af.active = true WHERE af.id = p_id;
    UPDATE advanced_filter af SET af.active = false WHERE af.id != p_id AND af.user_id = p_requester_id;
END BEGIN_PROC //
DELIMITER ;