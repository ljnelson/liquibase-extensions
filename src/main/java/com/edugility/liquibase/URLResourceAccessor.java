/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2014-2015 Edugility LLC.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * The original copy of this license is available at
 * http://www.opensource.org/license/mit-license.html.
 */
package com.edugility.liquibase;

import java.io.File; // for javadoc only
import java.io.InputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.Set;

import liquibase.resource.AbstractResourceAccessor;
import liquibase.resource.CompositeResourceAccessor; // for javadoc only
import liquibase.resource.ResourceAccessor;

/**
 * An {@link AbstractResourceAccessor} that delegates most of its work
 * to a delegate {@link ResourceAccessor}, while preserving the
 * ability to process {@link String}s that represent {@link URL}s.
 *
 * <h2>Design Notes</h2>
 *
 * <p>The Liquibase 3.3.0 {@link ResourceAccessor} interface is
 * inconsistently documented, and mixes several quite unrelated
 * concerns together.  The {@link CompositeResourceAccessor}
 * implicitly relies on non-{@code null} values being returned from
 * some of its aggregated {@link ResourceAccessor}s' methods.  The
 * combination of these two unfortunate facts means that returning
 * {@code null} from methods that do not make sense for a {@link
 * ResourceAccessor} like this one that processes URLs is effectively
 * impossible.  Consequently, a delegating approach has to be taken
 * instead.</p>
 *
 * @author <a href="http://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see AbstractResourceAccessor
 *
 * @see CompositeResourceAccessor
 *
 * @see ResourceAccessor
 */
public class URLResourceAccessor extends AbstractResourceAccessor {

  /**
   * The non-{@code null} {@link ResourceAccessor} to which the {@link
   * #toClassLoader()} method is delegated.
   *
   * <p>This field is never {@code null}.</p>
   *
   * @see #URLResourceAccessor(ResourceAccessor)
   */
  private final ResourceAccessor delegate;

  /**
   * Creates a new {@link URLResourceAccessor}.
   *
   * @param delegate the {@link ResourceAccessor} to which the {@link
   * #toClassLoader()} method will be delegated; must not be {@code
   * null}
   *
   * @exception IllegalArgumentException if {@code delegate} is {@code
   * null}
   */
  public URLResourceAccessor(final ResourceAccessor delegate) {
    super();
    if (delegate == null) {
      throw new IllegalArgumentException("delegate", new NullPointerException("delegate"));
    }
    this.delegate = delegate;
    this.init();
  }

  /**
   * Returns a {@link Set} of <strong>{@linkplain InputStream#close()
   * open}</strong> {@link InputStream}s that the supplied {@code
   * path} logically designates, or {@code null} if there is no
   * resource that can be identified with the supplied {@code path},
   * per the contract of the {@link
   * ResourceAccessor#getResourcesAsStream(String)} method.
   *
   * <p>This method may return {@code null} if there is no resource
   * that can be identified by the supplied {@code path}.</p>
   *
   * <p>This implementation attempts to {@linkplain URL#URL(String)
   * create a <code>URL</code> from the supplied <code>path</code>},
   * and, if that operation is successful, to return a {@linkplain
   * Collections#singleton(Object) <code>Set</code> containing only}
   * that {@code URL}.  If a {@link MalformedURLException} is
   * encountered during this operation, then {@code null} is returned
   * instead.</p>
   *
   * @param path a {@link String} that will be passed unaltered to the
   * {@linkplain URL#URL(String) constructor of the <code>URL</code>
   * class}; must not be {@code null}
   *
   * @return a {@link Set} of <strong>{@linkplain InputStream#close()
   * open}</strong> {@link InputStream}s, or {@code null}
   *
   * @exception NullPointerException if {@code path} is {@code null}
   *
   * @exception IOException if an input/output error occurs
   *
   * @see ResourceAccessor#getResourcesAsStream(String)
   */
  @Override
  public Set<InputStream> getResourcesAsStream(final String path) throws IOException {
    final Set<InputStream> returnValue;
    if (path == null) {
      returnValue = null;
    } else {
      Set<InputStream> temp = null;
      try {
        temp = Collections.singleton(new URL(path).openStream());
      } catch (final MalformedURLException returnNullInsteadPerContract) {
        temp = null;
      } finally {
        returnValue = temp;
      }
    }
    return returnValue;
  }

  /**
   * Returns {@code null} when invoked.
   *
   * <p>The {@link ResourceAccessor} interface does not document the
   * purpose of this method that subclasses are required to implement
   * or all of its parameters.  Consequently the behavior of this
   * implementation may be incorrect.  Use it with care.</p>
   *
   * @param relativeTo ignored
   *
   * @param path ignored
   *
   * @param includeFiles ignored
   *
   * @param includeDirectories ignored
   *
   * @param recursive ignored
   *
   * @return {@code null} when invoked
   *
   * @exception IOException if an error occurs
   *
   * @see ResourceAccessor#list(String, String, boolean, boolean, boolean)
   */
  @Override
  public Set<String> list(final String relativeTo, final String path, final boolean includeFiles, final boolean includeDirectories, final boolean recursive) throws IOException {
    return null;
  }

  /**
   * Returns the result of invoking the {@link
   * ResourceAccessor#toClassLoader()} method on the {@link
   * ResourceAccessor} supplied at {@linkplain
   * #URLResourceAccessor(ResourceAccessor) construction time}.
   *
   * @return a {@link ClassLoader}, or {@code null}
   *
   * @see ResourceAccessor#toClassLoader()
   */
  @Override
  public ClassLoader toClassLoader() {
    if (this.delegate == null) {
      return null;
    } else {
      return this.delegate.toClassLoader();
    }
  }
  
}
