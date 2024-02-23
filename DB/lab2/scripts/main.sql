-- 1
select "Н_ТИПЫ_ВЕДОМОСТЕЙ"."ИД", "Н_ВЕДОМОСТИ"."ДАТА"
from "Н_ТИПЫ_ВЕДОМОСТЕЙ"
         left join "Н_ВЕДОМОСТИ"
                   on "Н_ТИПЫ_ВЕДОМОСТЕЙ"."ИД" = 1 and "Н_ВЕДОМОСТИ"."ИД" < 1426978;


-- 2
select "Н_ЛЮДИ"."ИМЯ", "Н_ВЕДОМОСТИ"."ЧЛВК_ИД", "Н_СЕССИЯ"."УЧГОД"
from "Н_ЛЮДИ"
         right join "Н_ВЕДОМОСТИ"
                    on "Н_ВЕДОМОСТИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
         right join "Н_СЕССИЯ"
                    on "Н_СЕССИЯ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
where "Н_ЛЮДИ"."ИМЯ" < 'Роман'
  and "Н_ВЕДОМОСТИ"."ДАТА" = '2022-06-08'
  and "Н_СЕССИЯ"."ДАТА" > '2012-01-25';


-- 3
select count(*)
from (select count(*)
      from "Н_ЛЮДИ"
      where "ДАТА_РОЖДЕНИЯ" is not null
      group by "ИД") as tmp;

-- 4
/*
В таблице Н_ГРУППЫ_ПЛАНОВ найти номера планов,
по которым обучается (обучалось) более 2 групп на заочной форме обучения.
Для реализации использовать соединение таблиц.
 */
SELECT gr_plan."ГРУППА", gr_plan."ПЛАН_ИД"
FROM "Н_ГРУППЫ_ПЛАНОВ" gr_plan
         JOIN "Н_ПЛАНЫ" pl ON gr_plan."ПЛАН_ИД" = pl."ПЛАН_ИД"
         join "Н_ФОРМЫ_ОБУЧЕНИЯ" fo on fo."ИД" = pl."ФО_ИД";

select plan_id, form_id
from "Н_ГРУППЫ_ПЛАНОВ" gr_pl
         join (select plans."ИД" as plan_id, fo."ИД" as form_id
               from "Н_ПЛАНЫ" plans
                        join "Н_ФОРМЫ_ОБУЧЕНИЯ" fo on plans."ФО_ИД" = fo."ИД") as a on gr_pl."ПЛАН_ИД" = a.plan_id
where form_id = 1
group by form_id, a.plan_id
having count(*) > 2;
-- where pl."ФО_ИД" = 3
-- GROUP BY gr_plan."ПЛАН_ИД"
-- HAVING COUNT(*) > 2;

