select * from(select II2.ITEM_NO as PARENT, II1.ITEM_NO as CHILD
from CB02{{business}}.ITEM_INFO ii1, CB02{{business}}.ITEM_INFO ii2, CB02{{business}}.ITEM_GROUPS ig
where IG.ITEM_INFO_ID = II1.ITEM_INFO_ID
and IG.ITEM_GROUP_ID = II2.ITEM_INFO_ID) where rownum<=1000
