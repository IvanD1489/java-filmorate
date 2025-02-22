-- Genres --
merge into genres t
using (select 'Комедия' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into genres t
using (select 'Драма' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into genres t
using (select 'Мультфильм' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into genres t
using (select 'Триллер' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into genres t
using (select 'Документальный' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into genres t
using (select 'Боевик' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

-- Ratings --
merge into ratings t
using (select 'G' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into ratings t
using (select 'PG' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into ratings t
using (select 'PG-13' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into ratings t
using (select 'R' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);

merge into ratings t
using (select 'NC-17' as name) s
on s.name = t.name
when not matched then insert (name) values (s.name);