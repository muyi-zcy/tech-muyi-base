/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.muyi.db;

import com.google.common.base.Joiner;
import io.opentracing.tag.Tags;
import org.apache.shardingsphere.spi.database.metadata.DataSourceMetaData;
import org.apache.shardingsphere.underlying.executor.hook.SQLExecutionHook;
import org.springframework.beans.factory.annotation.Autowired;
import tech.muyi.core.MySpan;
import tech.muyi.core.MyTracer;
import tech.muyi.db.constant.ShardingTags;
import tech.muyi.util.ApplicationContextUtil;

import java.util.List;
import java.util.Map;

/**
 * Open tracing SQL execution hook.
 */
public final class OpenTracingSQLExecutionHook implements SQLExecutionHook {

    private static final String OPERATION_NAME = "/" + ShardingTags.COMPONENT_NAME + "/executeSQL/";

    private MySpan span;

    private MyTracer myTracer;


    @Override
    public void start(final String dataSourceName, final String sql, final List<Object> parameters,
                      final DataSourceMetaData dataSourceMetaData, final boolean isTrunkThread, final Map<String, Object> shardingExecuteDataMap) {


        myTracer = ApplicationContextUtil.getBean(MyTracer.class);
        if(myTracer == null){
            return;
        }

        MySpan parentSpan = myTracer.activeSpan();
        span = myTracer.buildSpan(OPERATION_NAME)
                .asChildOf(parentSpan)
                .withTag(Tags.COMPONENT.getKey(), ShardingTags.COMPONENT_NAME)
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT)
                .withTag(Tags.PEER_HOSTNAME.getKey(), dataSourceMetaData.getHostName())
                .withTag(Tags.PEER_PORT.getKey(), dataSourceMetaData.getPort())
                .withTag(Tags.DB_TYPE.getKey(), "sql")
                .withTag(Tags.DB_INSTANCE.getKey(), dataSourceName)
                .withTag(Tags.DB_STATEMENT.getKey(), sql)
                .withTag(ShardingTags.DB_BIND_VARIABLES.getKey(), toString(parameters)).start();

        myTracer.activateSpan(span);
    }

    private String toString(final List<Object> parameterSets) {
        if (null == parameterSets || parameterSets.isEmpty()) {
            return "";
        }
        return String.format("[%s]", Joiner.on(", ").useForNull("Null").join(parameterSets));
    }

    @Override
    public void finishSuccess() {
        if(myTracer == null){
            return;
        }
        this.myTracer.scopeManager().close();
        span.finish();
    }

    @Override
    public void finishFailure(final Exception cause) {
        if(myTracer == null){
            return;
        }
        ShardingErrorSpan.setError(span, cause);
        this.myTracer.scopeManager().close();
        span.finish();
    }
}
