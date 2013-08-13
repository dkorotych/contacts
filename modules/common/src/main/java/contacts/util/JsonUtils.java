/*
 * Copyright 2013 Dmitry Korotych &lt;dkorotych at exadel dot com&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package contacts.util;

import org.springframework.http.MediaType;
import java.nio.charset.Charset;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Dmitry Korotych &lt;dkorotych at exadel dot com&gt;
 */
public class JsonUtils {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private JsonUtils() {
    }

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
