/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Label;
import com.jcabi.github.Labels;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github labels.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "repo", "ticket" })
final class MkIssueLabels implements Labels {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Repo name.
     */
    private final transient Coordinates repo;

    /**
     * Issue number.
     */
    private final transient int ticket;

    /**
     * Public ctor.
     * @param stg Storage
     * @param rep Repo
     * @param issue Issue number
     * @checkstyle ParameterNumber (5 lines)
     */
    MkIssueLabels(final MkStorage stg, final Coordinates rep, final int issue) {
        this.storage = stg;
        this.repo = rep;
        this.ticket = issue;
    }

    @Override
    public void add(final Iterable<Label> labels) throws IOException {
        final Directives dirs = new Directives().xpath(this.xpath());
        for (final Label label : labels) {
            dirs.add("label")
                .add("name").set(label.name()).up()
                .add("color").set(label.color()).up().up();
        }
        this.storage.apply(dirs);
    }

    @Override
    public Iterable<Label> iterate() {
        throw new UnsupportedOperationException("#iterate()");
    }

    @Override
    public void remove(final String name) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("label[name='%s']", name)
            ).remove()
        );
    }

    @Override
    public void clear() throws IOException {
        this.storage.apply(
            new Directives().xpath("label[name]").remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/issues/issue[number='%d']/labels",
            this.repo, this.ticket
        );
    }

}
