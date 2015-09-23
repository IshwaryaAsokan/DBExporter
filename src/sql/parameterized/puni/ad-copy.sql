select I.ITEM as ITEM_NO, ACT.AD_COPY_TYPE as TYPE, AC.AD_COPY as VALUE
from CB02{{business}}.AD_COPY ac, CB02{{business}}.AD_COPY_TYPES act, CB02{{business}}.ITEMS i
where AC.AD_COPY_TYPE_ID = ACT.AD_COPY_TYPE_ID
and I.ITEM_ID = AC.ITEM_ID
order by I.ITEM
