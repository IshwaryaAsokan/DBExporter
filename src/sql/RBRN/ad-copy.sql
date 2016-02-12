select II.ITEM_NO, ACT.AD_COPY_TYPE as TYPE, ACC.AD_COPY as VALUE
from CB02RBRN.ITEM_INFO ii, CB02RBRN.AD_COPY ac, CB02RBRN.AD_COPY_TYPES act, CB02RBRN.AD_COPY_CHUNKS acc
where II.ITEM_INFO_ID = AC.ITEM_INFO_ID
and AC.AD_COPY_TYPE_ID = ACT.AD_COPY_TYPE_ID
and ACC.AD_COPY_ID = AC.AD_COPY_ID
order by II.ITEM_NO