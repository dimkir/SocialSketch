package org.socialsketch.codeposter.examples;

/*
 * Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.OAuthService;
import org.socialsketch.codeposter.CodeUtils;
import org.socialsketch.codeposter.Credentials;

/**
 * Create a Gist using an OAuth2 token
 */
public class GistExample2 {

	/**
	 * Request an OAuth2 token with 'gist' scope and then read a {@link Gist}
	 * using the granted token
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		OAuthService oauthService = new OAuthService();

		// Replace with actual login and password
                Credentials cred = CodeUtils.spawnBlankCredentials().initFromEnvironment();
		oauthService.getClient().setCredentials(cred.getUserName(), cred.getPassword());

		// Create authorization with 'gist' scope only
		Authorization auth = new Authorization();
		auth.setScopes(Arrays.asList("gist"));
		auth = oauthService.createAuthorization(auth);

		// Create Gist service configured with OAuth2 token
		GistService gistService = new GistService();
		gistService.getClient().setOAuth2Token(auth.getToken());

                Gist gist = new Gist();
                
		// Create Gist
//                        gist.setPublic(false);
//                        gist.setDescription("Created using OAuth2 token via Java API");
//                        GistFile file = new GistFile();
//                        file.setContent("Gist!");
//                        file.setFilename("gist.txt");
//                        gist.setFiles(Collections.singletonMap(file.getFilename(), file));
//                        gist = gistService.createGist(gist);
//                        System.out.println("Created Gist at " + gist.getHtmlUrl());
                
                //
                String gistId = "7336272";
                System.out  .println("Fetching gist with id: " + gistId);
                gist = gistService.getGist(gistId);
                CodeUtils.fileListToConsole(gist);
                
                
                
	}
    
}