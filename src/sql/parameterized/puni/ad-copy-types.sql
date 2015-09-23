select distinct ACT.AD_COPY_TYPE as TYPE
from CB02{{business}}.AD_COPY ac, CB02{{business}}.AD_COPY_TYPES act, CB02{{business}}.ITEMS i
where AC.AD_COPY_TYPE_ID = ACT.AD_COPY_TYPE_ID
and I.ITEM_ID = AC.ITEM_ID
