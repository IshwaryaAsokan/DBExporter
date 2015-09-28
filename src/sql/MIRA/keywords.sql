select II.ITEM_NO, KT.KEYWORD_TYPE as TYPE, KP.PHRASE as VALUE
from CB02MIRA.KEYWORDS k, CB02MIRA.KEYWORD_TYPES kt, CB02MIRA.KEYWORD_PHRASES kp, CB02MIRA.ITEM_INFO ii
where II.ITEM_INFO_ID = K.ITEM_INFO_ID
and K.KEYWORD_TYPE_ID = KT.KEYWORD_TYPE_ID
and K.PHRASE_ID = KP.PHRASE_ID
