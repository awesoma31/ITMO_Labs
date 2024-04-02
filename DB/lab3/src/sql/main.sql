insert into construction
values (4, 1, 2, 1);

insert into preferred_construction(construction_id, supporter_id)
values (4, 2);

delete from preferred_construction
where construction_id = 4;

select count(construction_id)
-- into supporters_amount
from preferred_construction pc
-- where pc.supporter_id = 4
group by pc.supporter_id;