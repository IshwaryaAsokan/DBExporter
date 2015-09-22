select I.ITEM as ITEM_NO, KT.KEYWORD_TYPE as TYPE, K.KEYWORD as VALUE
from CB02NBKR.ITEMS i, CB02NBKR.KEYWORDS k, CB02NBKR.KEYWORD_TYPES kt, CB02NBKR.KEYWORD_PHRASES kp
where k.keyword_type_id = kt.keyword_type_id
and k.keyword_phrase_id = kp.keyword_phrase_id
and I.ITEM_ID = K.ITEM_ID
