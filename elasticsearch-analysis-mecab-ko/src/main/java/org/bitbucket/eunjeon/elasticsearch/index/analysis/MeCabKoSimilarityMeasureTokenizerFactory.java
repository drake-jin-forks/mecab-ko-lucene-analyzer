/*******************************************************************************
 * Copyright 2013 Yongwoon Lee, Yungho Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.bitbucket.eunjeon.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.bitbucket.eunjeon.mecab_ko_lucene_analyzer.*;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

import java.io.IOException;
import java.io.Reader;

/**
 * 문서 유사도 측적용 tokenizer 팩토리 생성자. 다음과 같은 파라미터를 받는다. (실험적인)
 *   - mecab_args: mecab 옵션
 *   - compound_noun_min_length: 분해를 해야하는 복합명사의 최소 길이. 디폴트 값은 9999이다. (복합명사 분해 안함)
 *
 * @author bibreen <bibreen@gmail.com>
 */
public class MeCabKoSimilarityMeasureTokenizerFactory extends MeCabKoStandardTokenizerFactory {

  @Inject
  public MeCabKoSimilarityMeasureTokenizerFactory(
      Index index,
      @IndexSettings Settings indexSettings,
      @Assisted String name,
      @Assisted Settings settings) {
    super(index, indexSettings, name, settings);
    setCompoundNounMinLength(settings);
  }

  private void setCompoundNounMinLength(Settings settings) {
    compoundNounMinLength = settings.getAsInt(
        "compound_noun_min_length",
        TokenGenerator.NO_DECOMPOUND);
  }

  @Override
  public Tokenizer create(Reader reader) {
    return new MeCabKoTokenizer(
        reader,
        mecabArgs,
        new SimilarityMeasurePosAppender(),
        compoundNounMinLength);
  }

}
