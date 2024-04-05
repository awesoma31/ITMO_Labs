CREATE OR REPLACE FUNCTION count_supporters_amount(sup_id int)
    RETURNS int AS
$$
DECLARE
    supporters_amount int := null;
BEGIN
    select count(construction_id)
    into supporters_amount
    from preferred_construction pc
    where pc.supporter_id = sup_id
    group by pc.supporter_id;

    if not FOUND then
        raise exception 'supporter with such id not found';
    end if;

    return supporters_amount;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION update_construction()
    RETURNS trigger AS
$$
DECLARE
    sup_id_to_update int := null;
    sup_amount       int := null;
BEGIN
    SELECT pc.supporter_id
    into sup_id_to_update
    from preferred_construction pc
    where pc.supporter_id = NEW.supporter_id;

    sup_amount := count_supporters_amount(sup_id_to_update);

    update construction
    set supporters_amount = sup_amount
    where construction.id = sup_id_to_update;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER supporters_amount_trigger
    AFTER INSERT OR UPDATE or delete
    ON preferred_construction
    FOR EACH ROW
EXECUTE function update_construction();