/*
 * Copyright (c) BSI Business Systems Integration AG. All rights reserved.
 * http://www.bsiag.com/
 */

const caster = require('svg-bounding-box');
const fs = require('fs');
const os = require('os');
const path = require('path');
const webfontsGenerator = require('@vusion/webfonts-generator');

function errorHandler(e) {
  process.stderr.write(String(e) + '\n');
  process.exit(1);
}

try {
  const fontName = 'my-font-name';
  const svgSrc = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><g><path d="M11.987,24.003c-1.861,0-3.375-1.514-3.375-3.375s1.514-3.375,3.375-3.375s3.375,1.514,3.375,3.375 S13.848,24.003,11.987,24.003z M11.987,18.753c-1.034,0-1.875,0.841-1.875,1.875s0.841,1.875,1.875,1.875 c1.034,0,1.875-0.841,1.875-1.875S13.021,18.753,11.987,18.753z"/><path d="M11.987,6.753c-1.861,0-3.375-1.514-3.375-3.375s1.514-3.375,3.375-3.375s3.375,1.514,3.375,3.375 S13.848,6.753,11.987,6.753z M11.987,1.503c-1.034,0-1.875,0.841-1.875,1.875s0.841,1.875,1.875,1.875 c1.034,0,1.875-0.841,1.875-1.875S13.021,1.503,11.987,1.503z"/><path d="M11.987,15.378c-1.861,0-3.375-1.514-3.375-3.375c0-1.861,1.514-3.375,3.375-3.375s3.375,1.514,3.375,3.375 C15.362,13.864,13.848,15.378,11.987,15.378z M11.987,10.128c-1.034,0-1.875,0.841-1.875,1.875s0.841,1.875,1.875,1.875 c1.034,0,1.875-0.841,1.875-1.875S13.021,10.128,11.987,10.128z"/></g></svg>',
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><g><path d="M3,20.5c-1.241,0-2.25-1.009-2.25-2.25v-12c0-0.504,0.164-0.981,0.476-1.38c0.007-0.011,0.015-0.022,0.022-0.031 c0.006-0.008,0.015-0.018,0.023-0.027C1.696,4.298,2.327,4,3,4h18c0.671,0,1.301,0.297,1.731,0.814 c0.01,0.01,0.019,0.021,0.026,0.031c0.006,0.007,0.012,0.017,0.018,0.026C23.086,5.27,23.25,5.746,23.25,6.25v12 c0,1.241-1.009,2.25-2.25,2.25H3z M2.25,18.25C2.25,18.664,2.586,19,3,19h18c0.414,0,0.75-0.336,0.75-0.75V6.562l-7.276,5.596 C13.77,12.7,12.891,12.999,12,12.999c-0.891,0-1.77-0.299-2.474-0.841L2.25,6.562V18.25z M10.44,10.969 c0.444,0.342,0.998,0.53,1.56,0.53s1.115-0.188,1.559-0.53l7.111-5.47H3.329L10.44,10.969z"/></g></svg>'];
  // const svgSrc = ['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><circle class="a" cx="11.987" cy="20.628" r="2.625"/><circle class="a" cx="11.987" cy="3.378" r="2.625"/><circle class="a" cx="11.987" cy="12.003" r="2.625"/></svg>',
  //   '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><rect x="1.5" y="4.75" width="21" height="15" rx="1.5" ry="1.5"/><path d="M22.161,5.3l-8.144,6.264a3.308,3.308,0,0,1-4.034,0L1.839,5.3"/></svg>'];

  let num = 0;
  const tmpDir = path.join(os.tmpdir(), 'scout-font-gen'/* + Date.now()*/);
  if (!fs.existsSync(tmpDir)) {
    fs.mkdirSync(tmpDir);
  }

  svgSrc.forEach(s => caster(s)
    .then(viewBox => s.replace('viewBox="0 0 24 24"', 'viewBox="' + viewBox + '"'))
    .then(svg => fs.writeFileSync(path.join(tmpDir, 'out' + (num++) + '.svg'), svg))
    .catch(errorHandler));

  webfontsGenerator({
    files: [
      tmpDir + '/out0.svg',
      tmpDir + '/out1.svg'
    ],
    dest: path.join(tmpDir, 'dest')
  }, error => {
    if (error) {
      console.log('Fail!', error);
    } else {
      console.log('Done!');
    }
  });

} catch (e) {
  errorHandler(e);
}
