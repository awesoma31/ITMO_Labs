CREATE OR REPLACE FUNCTION avg_legs_amount(sup_id int)
    RETURNS float AS
$avg_legs$
declare
    avg_legs int;
BEGIN
    avg_legs := 0;
    select *
    from supporter
             join preferred_construction on supporter.id = preferred_construction.supporter_id
    where supporter.id = sup_id;
    return avg_legs;

END;
$avg_legs$ LANGUAGE plpgsql;


