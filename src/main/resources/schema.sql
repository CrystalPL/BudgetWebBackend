DELIMITER //

DROP PROCEDURE IF EXISTS activate_filter //

CREATE PROCEDURE activate_filter(
    IN p_id BIGINT,
    IN p_requester_id BIGINT
)
BEGIN
    DECLARE filter_exists BOOLEAN DEFAULT FALSE;
    DECLARE filter_active BOOLEAN DEFAULT FALSE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET filter_exists = FALSE;

    SELECT TRUE, af.active
    INTO filter_exists, filter_active
    FROM advanced_filter af
    WHERE af.id = p_id
      AND af.user_id = p_requester_id
    LIMIT 1;

    IF filter_exists THEN
        IF filter_active THEN
            UPDATE advanced_filter af SET af.active = false WHERE af.id = p_id;
        ELSE
            UPDATE advanced_filter af SET af.active = false WHERE af.user_id = p_requester_id;
            UPDATE advanced_filter af SET af.active = true WHERE af.id = p_id;
        END IF;
    END IF;

    SELECT filter_exists;
END //

DELIMITER ;