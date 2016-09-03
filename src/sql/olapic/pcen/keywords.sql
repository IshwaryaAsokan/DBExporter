select * from(select II.ITEM_NO, KT.KEYWORD_TYPE as TYPE, KP.PHRASE as VALUE
from CB02{{business}}.KEYWORDS k, CB02{{business}}.KEYWORD_TYPES kt, CB02{{business}}.KEYWORD_PHRASES kp, CB02{{business}}.ITEM_INFO ii
where II.ITEM_INFO_ID = K.ITEM_INFO_ID
and K.KEYWORD_TYPE_ID = KT.KEYWORD_TYPE_ID
and K.PHRASE_ID = KP.PHRASE_ID) where rownum<=1000
