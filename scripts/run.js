const core = require('@actions/core');
const { execSync } = require('child_process');
const path = require('path');

async function main() {
    try {
        const classFiles = findDayClassFiles();
        if (classFiles.length === 0) {
            console.log('No compiled classes named "Day*.class" found in out/src.');
            return;
        }

        const resultsByDay = {};
        for (const filePath of classFiles) {
            const { dayNum, result } = runDayClass(filePath);
            if (!dayNum)
                continue;
            if (!resultsByDay[dayNum]) resultsByDay[dayNum] = [];
            resultsByDay[dayNum].push(result);
        }

        buildSummary(resultsByDay);
    } catch (err) {
        core.setFailed(`Script error: ${err.message}`);
    }
}

function findDayClassFiles() {
    let listing = '';
    try {
        listing = execSync('ls out/src/Day*.class 2>/dev/null | sort -V', {
            encoding: 'utf-8',
        }).trim();
    } catch { }
    return listing ? listing.split('\n') : [];
}

function runDayClass(filePath) {
    const className = path.basename(filePath, '.class');
    const match = className.match(/^Day(\d+)/);
    if (!match) return { dayNum: null, result: null };

    const dayNum = match[1];
    console.log(`Running ${className} (Day ${dayNum})...`);

    const start = Date.now();
    let exitCode = 0;
    let stdout = '';
    try {
        stdout = execSync(`java -cp out/src ${className}`, { encoding: 'utf-8' });
    } catch (error) {
        exitCode = error.status || 1;
        stdout = error.stdout?.toString() || '';
    }
    const duration = Date.now() - start;

    console.log(stdout);
    const status = exitCode === 0 ? 'Success' : 'Failed';
    console.log(`${status} [${duration} ms]\n`);

    return {
        dayNum,
        result: { className, status, duration },
    };
}

function buildSummary(resultsByDay) {
    core.summary.addHeading('Build & Run Java Files - Summary', 2);

    const sortedDays = Object.keys(resultsByDay).sort((a, b) => +a - +b);
    for (const day of sortedDays) {
        core.summary.addHeading(`Day ${day}`, 3);
        const tableData = [
            [
                { data: 'Class Name 🗒️', header: true },
                { data: 'Status', header: true },
                { data: 'Duration (ms) ⏱️', header: true },
            ],
        ];
        for (const row of resultsByDay[day]) {
            tableData.push([`\`${row.className}\``, row.status == 'Success' ? '✅' : '⛔', row.duration.toString()]);
        }
        core.summary.addTable(tableData);
    }

    return core.summary.write();
}

main();
