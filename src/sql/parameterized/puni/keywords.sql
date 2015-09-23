select I.ITEM as ITEM_NO, KT.KEYWORD_TYPE as TYPE, KP.KEYWORD_PHRASE as VALUE
from CB02{{business}}.ITEMS i, CB02{{business}}.KEYWORDS k, CB02{{business}}.KEYWORD_TYPES kt, CB02{{business}}.KEYWORD_PHRASES kp
where k.keyword_type_id = kt.keyword_type_id
and k.keyword_phrase_id = kp.keyword_phrase_id
and I.ITEM_ID = K.ITEM_ID
