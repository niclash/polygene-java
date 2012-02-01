/*
 * Copyright (c) 2012, Niclas Hedhman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qi4j.spi.metrics;

public class NullMetricsProvider
    implements MetricsProvider
{
    private static final MetricsCounterFactory NULL_COUNTER_FACTORY = new NullMetricsFactory.NullCounterFactory();
    private static final MetricsGaugeFactory NULL_GAUGE_FACTORY = new NullMetricsFactory.NullGaugeFactory();
    private static final MetricsMeterFactory NULL_METER_FACTORY = new NullMetricsFactory.NullMeterFactory();
    private static final MetricsHistogramFactory NULL_HISTOGRAM_FACTORY = new NullMetricsFactory.NullHistogramFactory();
    private static final MetricsTimerFactory NULL_TIMER_FACTORY = new NullMetricsFactory.NullTimerFactory();
    private static final MetricsHealthCheckFactory NULL_HEALTHCHECK_FACTORY = new NullMetricsFactory.NullHealthCheckFactory();

    @Override
    public <T extends MetricsFactory> T createFactory( Class<T> factoryType )
    {
        if( factoryType.equals( MetricsCounterFactory.class ) )
        {
            return (T) NULL_COUNTER_FACTORY;
        }
        else if( factoryType.equals( MetricsGaugeFactory.class ) )
        {
            return (T) NULL_GAUGE_FACTORY;
        }
        else if( factoryType.equals( MetricsHealthCheckFactory.class ) )
        {
            return (T) NULL_HEALTHCHECK_FACTORY;
        }
        else if( factoryType.equals( MetricsHistogramFactory.class ) )
        {
            return (T) NULL_HISTOGRAM_FACTORY;
        }
        else if( factoryType.equals( MetricsMeterFactory.class ) )
        {
            return (T) NULL_METER_FACTORY;
        }
        else if( factoryType.equals( MetricsTimerFactory.class ) )
        {
            return (T) NULL_TIMER_FACTORY;
        }
        return null;
    }
}
