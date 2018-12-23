-- :name query-paintings :? :*
-- :doc Retrieve some paintings
select t.*
from paintings t, paintings_concepts t2
where t.id = t2.painting_id
and t.form = :form
--~(when (:))
-- limit 10
