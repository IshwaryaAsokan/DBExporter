select distinct ACT.AD_COPY_TYPE as TYPE
from CB02KPNA.ITEM_INFO ii, CB02KPNA.AD_COPY ac, CB02KPNA.AD_COPY_TYPES act, CB02KPNA.AD_COPY_CHUNKS acc
where II.ITEM_INFO_ID = AC.ITEM_INFO_ID
and AC.AD_COPY_TYPE_ID = ACT.AD_COPY_TYPE_ID
and ACC.AD_COPY_ID = AC.AD_COPY_ID
